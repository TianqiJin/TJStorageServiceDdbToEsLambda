package com.tianqj.tjstorageservice.dagger;

import com.google.gson.Gson;
import com.tianqj.tjstorageservice.utils.AwsRequestSigningInterceptor;
import com.tianqj.tjstorageservice.utils.Constants;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverter;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverterImpl;
import com.tianqj.tjstorageservice.client.OpenSearchClient;
import dagger.Module;
import dagger.Provides;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

import javax.inject.Singleton;

import static com.tianqj.tjstorageservice.utils.Constants.SERVICE_NAME;

@Log4j2
@Module
public class LambdaModule {
    @Provides
    @Singleton
    public Gson getGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public StsClient provideStsClient() {
        return StsClient.builder().region(Constants.REGION).build();
    }

    @Provides
    @Singleton
    public AwsCredentialsProvider getAwsCredentialsProvider(final StsClient stsClient) {
        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(Constants.LAMBDA_ROLE)
                .roleSessionName(Constants.LAMBDA_ROLE_SESSION)
                .build();
        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(() -> assumeRoleRequest)
                .build();
    }

    @Provides
    @Singleton
    public Aws4Signer getAws4Signer() {
        return Aws4Signer.create();
    }

    @Provides
    @Singleton
    public HttpRequestInterceptor getHttpRequestInterceptor(final Aws4Signer signer,
                                                            final AwsCredentialsProvider awsCredentialsProvider) {
        return new AwsRequestSigningInterceptor(
                SERVICE_NAME,
                signer,
                awsCredentialsProvider,
                Constants.REGION
        );
    }

    @Provides
    @Singleton
    public RestHighLevelClient provideRestHighLevelClient(final HttpRequestInterceptor httpRequestInterceptor) {

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(Constants.OPEN_SEARCH_DOMAIN, -1, "https"))
                        .setHttpClientConfigCallback(builder -> {
                            builder.useSystemProperties().addInterceptorLast(httpRequestInterceptor);
                            return builder;
                        })
                        .setRequestConfigCallback(requestConfigBuilder -> {
                            requestConfigBuilder
                                    .setConnectTimeout(
                                            Long.valueOf(Constants.OPEN_SEARCH_CONNECT_TIMEOUT.toMillis()).intValue())
                                    .setSocketTimeout(
                                            Long.valueOf(Constants.OPEN_SEARCH_SOCKET_TIMEOUT.toMillis()).intValue())
                                    .build();
                            return requestConfigBuilder;
                        }));
    }

    @Provides
    @Singleton
    public OpenSearchClient getOpenSearchClient(final RestHighLevelClient delegate,
                                                final JacksonConverter jacksonConverter) {

        return new OpenSearchClient(delegate, jacksonConverter);
    }

    @Provides
    @Singleton
    public JacksonConverter getJacksonConverter() {
        return new JacksonConverterImpl();
    }
}
