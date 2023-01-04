# JWT based authentication Demo

## Setting up the project
 - Clone the repo
 - Download and install [MySQL](https://dev.mysql.com/downloads/mysql/)
 - Start the MySQL server and create a db called `testDb`
 - Start the server by running `./gradlew bootRun`

## Users and Roles

Users can signup for a new account or login using existing username and password. While 
signing up users can assign themselves one of the three roles
- User
- Moderator
- Admin

To prefill the `Roles` table in the database hit the following api before using the other
APIs

`GET` : `http://localhost:8080/api/test/insert-roles`

This will insert the above 3 roles into the database.

## Creating a new account

We can signup for a new account by hitting the following API with the required details

`POST`: `http://localhost:8080/api/auth/signup`

with the following JSON body

```json
{
    "username": "jobinlawrance",
    "email": "jobinlawrance@gmail.com",
    "role": ["ROLE_USER"],
    "password": "password"
}
```

If we take a look at the `users` table in the db, we can see the details are saved and password is hashed

![Users table](users_table.jpeg)

## Accessing secure API

### Getting JWT Token

Inorder to get the JWT token we need to login using the credentials we created earlier.

`POST`: `http://localhost:8080/api/auth/signin`

with the following JSON body

```json
{
    "username": "jobinlawrance",
    "password": "password"
}
```

This will return us a similar response

```json
{
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2Jpbmxhd3JhbmNlIiwiaWF0IjoxNjcyODMyMDI5LCJleHAiOjE2NzI5MTg0Mjl9.JqmDzpD53FhpwzTXeRsL1VfNFJ-UmbVgEPmLK56fYXZEfnCjbTAg19DDh9UgEv75iIY_U8XyDFinhGn7xvjDMA",
    "id": 1,
    "username": "jobinlawrance",
    "email": "jobinlawrance@gmail.com",
    "roles": [
        "ROLE_USER"
    ],
    "tokenType": "Bearer"
}
```

We need to note down the above `accessToken` for further API calls

### Using the token

If we were to hit the following API without a token we will get a `401: Unauthorized` error.

`GET`: `http://localhost:8080/api/test/user`

Response without access token

```json
{
"path": "/api/test/mod",
"error": "Unauthorized",
"message": "Full authentication is required to access this resource",
"status": 401
}
```

Now if we make the same api call using the access token in the request header it succeeds.

![Bearer Token](bearer_token.png)

### Trying on different Role

Now if we try the same bearer token on a role that is above the current one i.e mod or admin then we get `403: Forbidden`

![Admin API](admin.png)

---