package io.polyglotted.common.test;

import io.polyglotted.common.model.AuthToken;
import io.polyglotted.common.model.Builder;
import io.polyglotted.common.model.Subject;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.polyglotted.common.model.AuthToken.tokenBuilder;
import static io.polyglotted.common.model.Builder.buildWith;
import static io.polyglotted.common.model.Subject.subjectBuilder;
import static io.polyglotted.common.util.BaseSerializer.deserialize;
import static io.polyglotted.common.util.ListBuilder.immutableList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class SubjectTokenTest {
    public static Object[][] jsonInputs() throws Exception {
        return new Object[][] {
            {"{\"enabled\":true,\"metadata\":{\"mfaEnabled\":false},\"roles\":[\"my_role1\"],\"username\":\"8e07e12f432dce131fc2005e29de5264\"}",
                subjectBuilder().usernameMd5("foo.bar.baz", "mister@misty.co").role("my_role1").metadata("mfaEnabled", false)},
            {"{\"username\":\"rdeniro\",\"roles\":[\"admin\",\"kibana4\"],\"full_name\":\"Jack Nicholson\"}",
                subjectBuilder().username("rdeniro").roles(immutableList("admin", "kibana4")).fullName("Jack Nicholson")},
            {"{\"accessToken\":\"y7nvAiCrpP8HRJkxgdb3s3T4XX8Dvx18C5FmT\",\"expiresIn\":1200,\"refreshToken\":\"fooBarBaz\",\"tokenType\":\"Bearer\"}",
                tokenBuilder().accessToken("y7nvAiCrpP8HRJkxgdb3s3T4XX8Dvx18C5FmT").expiresIn(1200).tokenType("Bearer").refreshToken("fooBarBaz")},
            {"{\"access_token\":\"y7nvAiCrpP8HRJkxgdb3s3T4XX8Dvx18C5FmT\",\"expires_in\":1200,\"refresh_token\":\"fooBarBaz\",\"type\":\"Bearer\"}",
                tokenBuilder().accessToken("y7nvAiCrpP8HRJkxgdb3s3T4XX8Dvx18C5FmT").expiresIn(1200).tokenType("Bearer").refreshToken("fooBarBaz")},
        };
    }

    @Test @Parameters(method = "jsonInputs") @SuppressWarnings("unchecked")
    public void serializeWithBuilder(String json, Builder<Object> builder) throws Exception {
        Object actual = buildWith(deserialize(json), builder.getClass());
        assertThat(json, actual, is(builder.build()));
    }

    public static Object[][] jsonInputs2() throws Exception {
        return new Object[][] {
            {"{\"enabled\":true,\"metadata\":{\"mfaEnabled\":false},\"roles\":[\"my_role1\"],\"username\":\"8e07e12f432dce131fc2005e29de5264\"}",
                subjectBuilder().usernameMd5("foo.bar.baz", "mister@misty.co").role("my_role1").metadata("mfaEnabled", false).build(), Subject.class},
            {"{\"accessToken\":\"y7nvAiCrpP8HRJkxg\",\"expiresIn\":1200,\"refreshToken\":\"fooBarBaz\",\"tokenType\":\"Bearer\"}",
                tokenBuilder().accessToken("y7nvAiCrpP8HRJkxg").expiresIn(1200).tokenType("Bearer").refreshToken("fooBarBaz").build(), AuthToken.class},
        };
    }

    @Test @Parameters(method = "jsonInputs2")
    public void serializeDirect(String json, Object expected, Class<?> clazz) throws Exception {
        assertThat(json, deserialize(json, clazz), is(expected));
    }
}