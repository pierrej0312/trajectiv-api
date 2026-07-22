package com.trajectiv.api.routes;

public final class ApiRoutes {

    private ApiRoutes() {
    }

    public static final String API_V1 = "/v1";

    public static final class V1 {
        private V1() {
        }

        public static final String SECURITY = API_V1 + "/security";

        public static final String ME = API_V1 + "/me";
        public static final String ME_PROFILE = ME + "/profile";
        public static final String ME_AVATAR = ME + "/avatar";
        public static final String ME_ONBOARDING = ME + "/onboarding";

        public static final String OPPORTUNITIES = API_V1 + "/opportunities";

        public static final String JOB_ROLES = API_V1 + "/job-roles";

        public static final String ORGANIZATIONS =
                API_V1 + "/organizations";

        public static final String ORGANIZATION_BY_ID =
                ORGANIZATIONS + "/{organizationId}";

        public static final String ORGANIZATION_INVITATIONS =
                ORGANIZATIONS
                        + "/{organizationId}/invitations";

        public static final String ORGANIZATION_INVITATION_ACCEPTANCE =
                API_V1 + "/organization-invitations";

        public static final String ORGANIZATION_MEMBERS =
                ORGANIZATIONS + "/{organizationId}/members";
    }
}