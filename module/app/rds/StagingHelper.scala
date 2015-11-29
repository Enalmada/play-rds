package rds

import com.amazonaws.auth.{InstanceProfileCredentialsProvider, EnvironmentVariableCredentialsProvider}
import com.amazonaws.services.rds.AmazonRDSClient
import com.amazonaws.services.rds.model._
import play.api.{Play, Logger}
import scala.collection.JavaConverters._
import play.api.Play.current

import scala.util.{Failure, Success, Try}

object StagingHelper {

  def waitForReady(rdsClient: AmazonRDSClient, instanceName: String): Boolean = {
    val sleepTime: Long = 30
    var count: Int = 0
    var renameReady = false
    Logger.info("Waiting for instance to be active...")
    Thread.sleep(30 * 1000) // It can take a while for status renaming to register
    Logger.debug("Done with initial sleep")
    while (!renameReady) {

      val status = try {
        val request: DescribeDBInstancesRequest = new DescribeDBInstancesRequest()
        request.setDBInstanceIdentifier(instanceName)
        val result = rdsClient.describeDBInstances(request)
        result.getDBInstances.get(0).getDBInstanceStatus
      } catch {
        case none: DBInstanceNotFoundException =>
          Logger.debug("No instance found yet...")
          "unavailable"
        case e: Exception => throw new Exception(e)
      }

      if (status.equalsIgnoreCase("available")) {
        renameReady = true
      }
      else {
        count = count + 1
        Thread.sleep(sleepTime * 1000)
      }
      if (count > 200) {
        throw new Exception("Active wait took too long.")
      }


    }
    Logger.info("Instance now active.")

    true
  }

  def refreshStaging: Try[(String, String)] = {
    Logger.info("Updating staging db...")

    try {

      val stagingName = Play.application.configuration.getString("staging.name").get
      val stagingMaster = Play.application.configuration.getString("staging.master").get
      val stagingOldName = stagingName + "-old"
      val stagingAZ = Play.application.configuration.getString("staging.az").get
      val stagingClass = Play.application.configuration.getString("staging.class").get
      val stagingSubnet = Play.application.configuration.getString("staging.subnet").get
      val stagingSec = List(Play.application.configuration.getString("staging.vpcSecGroupId").get).asJava

      // Locally you will need to have environment keys but production uses machine role
      // https://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-roles.html
      val rdsClient: AmazonRDSClient =
        if (Play.isDev)
          new AmazonRDSClient(new EnvironmentVariableCredentialsProvider())
        else
          new AmazonRDSClient(new InstanceProfileCredentialsProvider())

      val request: DescribeDBSnapshotsRequest = new DescribeDBSnapshotsRequest()
      request.setDBInstanceIdentifier(stagingMaster)
      // This is the min...stupid.
      request.setMaxRecords(20)
      val result: DescribeDBSnapshotsResult = rdsClient.describeDBSnapshots(request)
      val list: java.util.List[DBSnapshot] = result.getDBSnapshots
      Logger.debug(s"list length = ${list.size}")
      if (list.isEmpty) {
        Success("error" -> "No snapshots found.")
      } else {
        val snapshot = list.get(0)
        Logger.debug(s"Snapshot name: ${snapshot.getDBSnapshotIdentifier}")

        // Rename staging if exists
        val instanceRequest: DescribeDBInstancesRequest = new DescribeDBInstancesRequest()
        instanceRequest.setDBInstanceIdentifier(stagingName)

        try {
          val response = rdsClient.describeDBInstances(instanceRequest)
          if (response.getDBInstances.size() > 0) {
            // Assumption here is that renaming is quicker than deleting
            Logger.info("Renaming existing staging to old...")
            val modifyDBInstanceRequest: ModifyDBInstanceRequest = new ModifyDBInstanceRequest()
            modifyDBInstanceRequest.setDBInstanceIdentifier(stagingName)
            modifyDBInstanceRequest.setNewDBInstanceIdentifier(stagingOldName)
            modifyDBInstanceRequest.setApplyImmediately(true)
            rdsClient.modifyDBInstance(modifyDBInstanceRequest)
            val waitOver = waitForReady(rdsClient, stagingOldName)

            // Then delete old staging
            if (waitOver) {
              Logger.info("Deleting renamed old staging.")
              val deleteDBInstanceRequest = new DeleteDBInstanceRequest()
              deleteDBInstanceRequest.setDBInstanceIdentifier(stagingOldName)
              deleteDBInstanceRequest.setSkipFinalSnapshot(true)
              rdsClient.deleteDBInstance(deleteDBInstanceRequest)
            }
          }
        } catch {
          case none: DBInstanceNotFoundException => Logger.info("No staging found.")
        }


        // Restore latest snapshot as staging
        Logger.info("Restoring staging snapshot...")
        val restoreRequest = new RestoreDBInstanceFromDBSnapshotRequest()
        restoreRequest.setDBInstanceIdentifier(stagingName)
        restoreRequest.setDBSnapshotIdentifier(snapshot.getDBSnapshotIdentifier)
        restoreRequest.setAutoMinorVersionUpgrade(true)
        restoreRequest.setAvailabilityZone(stagingAZ)
        restoreRequest.setDBInstanceClass(stagingClass)
        restoreRequest.setMultiAZ(false)
        restoreRequest.setPubliclyAccessible(true)
        restoreRequest.setStorageType("gp2") // SSD
        restoreRequest.setDBSubnetGroupName(stagingSubnet)
        rdsClient.restoreDBInstanceFromDBSnapshot(restoreRequest)

        // Set backup window to 0
        val waitOver = waitForReady(rdsClient, stagingName)
        if (waitOver) {
          Logger.info("Modifying snapshot to desired settings...")
          val modifyDBInstanceRequest: ModifyDBInstanceRequest = new ModifyDBInstanceRequest()
          modifyDBInstanceRequest.setDBInstanceIdentifier(stagingName)
          modifyDBInstanceRequest.setBackupRetentionPeriod(0)
          modifyDBInstanceRequest.setApplyImmediately(true)
          modifyDBInstanceRequest.setVpcSecurityGroupIds(stagingSec)
          rdsClient.modifyDBInstance(modifyDBInstanceRequest)
        }

        Success("success" -> "Staging is refreshing now.  Monitor AWS for completion.")
      }

    } catch {
      case e: Exception => Failure(e)
    }

  }

}