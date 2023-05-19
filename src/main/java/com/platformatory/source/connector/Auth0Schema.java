package com.platformatory.source.connector;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

public class Auth0Schema {

    public static final String SCHEMA_NAME = "user_schema";

    // Define field names
    public static final String USER_ID_FIELD = "user_id";
    public static final String EMAIL_FIELD = "email";
    public static final String EMAIL_VERIFIED_FIELD = "email_verified";
    public static final String USERNAME_FIELD = "username";
    public static final String PHONE_NUMBER_FIELD = "phone_number";
    public static final String PHONE_VERIFIED_FIELD = "phone_verified";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String UPDATED_AT_FIELD = "updated_at";
    public static final String IDENTITIES_FIELD = "identities";
    public static final String APP_METADATA_FIELD = "app_metadata";
    public static final String USER_METADATA_FIELD = "user_metadata";
    public static final String PICTURE_FIELD = "picture";
    public static final String NAME_FIELD = "name";
    public static final String NICKNAME_FIELD = "nickname";
    public static final String MULTIFACTOR_FIELD = "multifactor";
    public static final String LAST_IP_FIELD = "last_ip";
    public static final String LAST_LOGIN_FIELD = "last_login";
    public static final String LOGINS_COUNT_FIELD = "logins_count";
    public static final String BLOCKED_FIELD = "blocked";
    public static final String GIVEN_NAME_FIELD = "given_name";
    public static final String FAMILY_NAME_FIELD = "family_name";

    public static final Schema SCHEMA = buildSchema();

    private static Schema buildSchema() {
        SchemaBuilder builder = SchemaBuilder.struct().name(SCHEMA_NAME)
                .field(USER_ID_FIELD, Schema.STRING_SCHEMA)
                .field(EMAIL_FIELD, Schema.STRING_SCHEMA)
                .field(EMAIL_VERIFIED_FIELD, Schema.BOOLEAN_SCHEMA)
                .field(USERNAME_FIELD, Schema.STRING_SCHEMA)
                .field(PHONE_NUMBER_FIELD, Schema.STRING_SCHEMA)
                .field(PHONE_VERIFIED_FIELD, Schema.BOOLEAN_SCHEMA)
                .field(CREATED_AT_FIELD, Schema.STRING_SCHEMA)
                .field(UPDATED_AT_FIELD, Schema.STRING_SCHEMA)
                .field(IDENTITIES_FIELD, SchemaBuilder.array(
                        SchemaBuilder.struct().name("identity_schema")
                                .field("connection", Schema.STRING_SCHEMA)
                                .field("user_id", Schema.STRING_SCHEMA)
                                .field("provider", Schema.STRING_SCHEMA)
                                .field("isSocial", Schema.BOOLEAN_SCHEMA)
                                .build()
                ).build())
                .field(APP_METADATA_FIELD, SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA))
                .field(USER_METADATA_FIELD, SchemaBuilder.map(Schema.STRING_SCHEMA, Schema.STRING_SCHEMA))
                .field(PICTURE_FIELD, Schema.STRING_SCHEMA)
                .field(NAME_FIELD, Schema.STRING_SCHEMA)
                .field(NICKNAME_FIELD, Schema.STRING_SCHEMA)
                .field(MULTIFACTOR_FIELD, SchemaBuilder.array(Schema.STRING_SCHEMA).build())
                .field(LAST_IP_FIELD, Schema.STRING_SCHEMA)
                .field(LAST_LOGIN_FIELD, Schema.STRING_SCHEMA)
                .field(LOGINS_COUNT_FIELD, Schema.INT32_SCHEMA)
                .field(BLOCKED_FIELD, Schema.BOOLEAN_SCHEMA)
                .field(GIVEN_NAME_FIELD, Schema.STRING_SCHEMA)
                .field(FAMILY_NAME_FIELD, Schema.STRING_SCHEMA);

        return builder.build();
    }

    public static Struct createStructFromData(UserData userData) {
        Struct struct = new Struct(SCHEMA)
                .put(USER_ID_FIELD, userData.getUserId())
                .put(EMAIL_FIELD, userData.getEmail())
                .put(EMAIL_VERIFIED_FIELD, userData.isEmailVerified())
                .put(USERNAME_FIELD, userData.getUsername())
                .put(PHONE_NUMBER_FIELD, userData.getPhoneNumber())
                .put(PHONE_VERIFIED_FIELD, userData.isPhoneVerified())
                .put(CREATED_AT_FIELD, userData.getCreatedAt())
                .put(UPDATED_AT_FIELD, userData.getUpdatedAt())
                .put(IDENTITIES_FIELD, createIdentitiesArray(userData.getIdentities()))
                .put(APP_METADATA_FIELD, userData.getAppMetadata())
                .put(USER_METADATA_FIELD, userData.getUserMetadata())
                .put(PICTURE_FIELD, userData.getPicture())
                .put(NAME_FIELD, userData.getName())
                .put(NICKNAME_FIELD, userData.getNickname())
                .put(MULTIFACTOR_FIELD, userData.getMultifactor())
                .put(LAST_IP_FIELD, userData.getLastIp())
                .put(LAST_LOGIN_FIELD, userData.getLastLogin())
                .put(LOGINS_COUNT_FIELD, userData.getLoginsCount())
                .put(BLOCKED_FIELD, userData.isBlocked())
                .put(GIVEN_NAME_FIELD, userData.getGivenName())
                .put(FAMILY_NAME_FIELD, userData.getFamilyName());

        return struct;
    }

    private static Object[] createIdentitiesArray(List<IdentityData> identities) {
        Object[] identitiesArray = new Object[identities.size()];

        for (int i = 0; i < identities.size(); i++) {
            IdentityData identityData = identities.get(i);
            Struct identityStruct = new Struct(SCHEMA.field(IDENTITIES_FIELD).schema().valueSchema())
                    .put("connection", identityData.getConnection())
                    .put("user_id", identityData.getUserId())
                    .put("provider", identityData.getProvider())
                    .put("isSocial", identityData.isSocial());

            identitiesArray[i] = identityStruct;
        }

        return identitiesArray;
    }

}
