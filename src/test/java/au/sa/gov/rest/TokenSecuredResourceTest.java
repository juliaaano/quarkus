package au.sa.gov.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
public class TokenSecuredResourceTest {

    @Test
    public void testHelloEndpoint() {
        Response response = given()
                .when()
                .get("/secured/permit-all")
                .andReturn();
        response.then()
                .statusCode(200)
                .body(containsString("hello + anonymous, isHttps: false, authScheme: null, hasJWT: false"));
    }

    @Test
    public void testHelloRolesAllowedUser() {
        Response response = given().auth()
                .oauth2(generateValidUserToken())
                .when()
                .get("/secured/roles-allowed").andReturn();
        response.then()
                .statusCode(200)
                .body(containsString("hello + jdoe@quarkus.io, isHttps: false, authScheme: Bearer, hasJWT: true"));
    }
    
    @Test
    public void testHelloRolesAllowedAdminOnlyWithUserRole() {
        Response response = given().auth()
                .oauth2(generateValidUserToken())
                .when()
                .get("/secured/roles-allowed-admin").andReturn();
        response.then().statusCode(403);
    }
    
    @Test
    public void testHelloRolesAllowedAdmin() {
        Response response = given().auth()
                .oauth2(generateValidAdminToken())
                .when()
                .get("/secured/roles-allowed").andReturn();
        response.then()
                .statusCode(200)
                .body(containsString("hello + jdoe@quarkus.io, isHttps: false, authScheme: Bearer, hasJWT: true"));
    }
    
    @Test
    public void testHelloRolesAllowedAdminOnlyWithAdminRole() {
        Response response = given().auth()
                .oauth2(generateValidAdminToken())
                .when()
                .get("/secured/roles-allowed-admin").andReturn();
        response.then()
                .statusCode(200)
                .body(containsString("Only admins should see this."));
    }
    
    @Test
    public void testHelloRolesAllowedInvalidAudience() {
        Response response = given().auth()
                .oauth2(generateInvalidAudienceToken())
                .when()
                .get("/secured/roles-allowed").andReturn();
        response.then().statusCode(401);
    }
    
    @Test
    public void testHelloRolesAllowedModifiedToken() {
        Response response = given().auth()
                .oauth2(generateValidUserToken() + "1")
                .when()
                .get("/secured/roles-allowed").andReturn();
        response.then().statusCode(401);
    }
    
    @Test
    public void testHelloRolesAllowedWrongIssuer() {
        Response response = given().auth()
                .oauth2(generateWrongIssuerToken())
                .when()
                .get("/secured/roles-allowed").andReturn();
        response.then().statusCode(401);
    }
    
    static String generateValidUserToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://example.com/issuer")
        		   .groups("user")
        		   .audience("theaudience")
        		   .sign();
    }
    
    static String generateValidAdminToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://example.com/issuer")
        		   .groups("admin")
        		   .audience("theaudience")
        		   .sign();
    }
    
    static String generateInvalidAudienceToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://example.com/issuer")
        		   .groups(new HashSet<>(Arrays.asList("user", "admin")))
        		   .audience("wrongaudience")
        		   .sign();
    }
    
    static String generateWrongIssuerToken() {
        return Jwt.upn("jdoe@quarkus.io")
        		   .issuer("https://wrong-issuer")
        		   .groups(new HashSet<>(Arrays.asList("user", "admin")))
                   .audience("theaudience")
        		   .sign();
    }
}
