## Izzy-city project ##

This project representing Rest API implemented by using Spring Boot.
It.s intended to manage (_rent, move. charge, etc._) of scooters in city.

The Project Development Environment is the following:

- Java 22.0.1
- Sping framework 3.3.1
- PostgreSQL 12.20
- Ubuntu OS 20.04.6 LTS

The API support the following parts of management:

- Admin management
- Authentication management
- User management
- Order management
- Task management
- Notification management
- Role management
- Zone management

The Authentication is based on the following principals

- The Access token (JWT) and Refresh token stored on the client-side cookies. They are generated during user login.
- The Refresh token is also stored on the server side with the purpose of verifying its validity and ownership by a specific user.
- The Access token from the client's cookie should be sent in the HTTP header using the Cookie parameter for each request requiring authorization access to resources.
- For security purposes, Access tokens are typically valid for a short period. Once they expire, the Refresh token must be used to renew/regenerate the access token.
- Certainly, the Refresh token must be valid when refreshing; otherwise, the user will have to go through the full sign-in procedure.

More details over requests can be found in [online API description](https://apidog.com/apidoc/shared-f273c0bb-43ad-4e2c-8933-b2b010936182)

Detailed document can be found in [backend design doc](https://docs.google.com/document/d/1BCJDxCQm1cxbGme3nD_mjg3hG_enfjJW)
