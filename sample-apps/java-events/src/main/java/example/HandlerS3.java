package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

// Handler value: example.Handler
public class HandlerS3 implements RequestHandler<S3Event, String>{

    private final SsmClient ssmClient = SsmClient.builder().region(Region.EU_NORTH_1).build();

    @Override
    public String handleRequest(S3Event event, Context context)
    {

        GetParameterRequest parameterRequest = GetParameterRequest.builder().name("/dev/bucket-name").build();
        GetParameterResponse parameter = ssmClient.getParameter(parameterRequest);
        LambdaLogger logger = context.getLogger();
        logger.log("PARMA VAL: "+ parameter.parameter().value());
        logger.log("EVENT: " + event);
        logger.log("CONTEXT: " + context);
        S3EventNotificationRecord record = event.getRecords().get(0);
        String srcBucket = record.getS3().getBucket().getName();
        // Object key may have spaces or unicode non-ASCII characters.
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
        logger.log("RECORD: " + record);
        logger.log("SOURCE BUCKET: " + srcBucket);
        logger.log("SOURCE KEY: " + srcKey);
        // log execution details
        return srcBucket + "/" + srcKey;
    }
}