package kr.jadekim.standingwater.aws.repository

import kr.jadekim.standingwater.repository.PresentationFileRepository
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

class PresentationFileRepositoryImpl(
    val bucket: String,
    private val accessKeyId: String,
    private val secretAccessKey: String
) : PresentationFileRepository {

//    companion object {
//        private const val ROLE_SESSION_NAME = "API-SERVER"
//    }
//
//    private val stsClient = StsClient.builder()
//        .region(Region.AP_NORTHEAST_2)
//        .build()
//
//    private val assumeRoleRequest = AssumeRoleRequest.builder()
//        .roleArn(roleArn)
//        .roleSessionName(ROLE_SESSION_NAME)
//        .build()
//
//    private val roleCredentialsProvider = StsAssumeRoleCredentialsProvider.builder()
//        .stsClient(stsClient)
//        .refreshRequest(assumeRoleRequest)
//        .build()

    private val s3Client = S3Client.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
        .build()

    override suspend fun saveFile(
        fileData: ByteArray,
        mimeType: String
    ): UUID {

        val id = UUID.randomUUID()

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key("file/$id")
                .build(),
            RequestBody.fromContentProvider({ fileData.inputStream() }, fileData.size.toLong(), mimeType)
        )

        return id
    }
}