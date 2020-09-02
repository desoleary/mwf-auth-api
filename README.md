## Authorization API (Built with Java Spring MVC, JWT, Hibernate & PostgreSQL)

#### Installation and running

```bash
git clone git@github.com:desoleary/mwf-auth-api.git
cd mwf-auth-api.git
./mvnw spring-boot:run # builds package and runs application, ensure to update application.properties with your db creds (PostgreSQL)
```

#### API (Requests)

###### Register User (Step One)

```bash
curl --location --request POST 'http://localhost:5000/api/auth/signup_step_one' \
--data-raw '{
    "email": "desoleary+testing@gmail.com",
    "name": "Desmond O'\''Leary"
}'
```

**Sample Response:**
```js
{
    "success": true,
    "message": "User registered successfully"
}
```

###### Register User (Step Two)

```bash
curl --location --request POST 'http://localhost:5000/api/auth/signup_step_two' \
--data-raw '{
    "email": "desoleary+testing@gmail.com",
    "password": "some-secret-password",
    "confirmationPassword": "some-secret-password"
}'
```

**Sample Response:**
```js
{
    "success": true,
    "message": "User fully registered successfully"
}
```

###### User Login

```bash
curl --location --request POST 'http://localhost:5000/api/auth/signin' \
--data-raw '{
    "email": "desoleary+testing@gmail.com",
    "password": "some-secret-password"
}'
```

**Sample Response:**
```js
{
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMiIsImlhdCI6MTU5OTA4Mjg2NSwiZXhwIjoxNTk5Njg3NjY1fQ.Bsm4ZnmPVuejsgpCl0zT6XseMFXmcVm4GyBuIHe-x8fXJfwzA84chJDGFsoHtVcMZaGwWo_0FL031CI9fQjuIQ",
    "tokenType": "Bearer"
}
```

###### Current User Details
```bash
curl --location --request GET 'http://localhost:5000/api/users/me' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMiIsImlhdCI6MTU5OTA4Mjg2NSwiZXhwIjoxNTk5Njg3NjY1fQ.Bsm4ZnmPVuejsgpCl0zT6XseMFXmcVm4GyBuIHe-x8fXJfwzA84chJDGFsoHtVcMZaGwWo_0FL031CI9fQjuIQ' \
--data-raw '{
    "email": "desoleary@gmail.com",
    "password": "some-secret-password"
}'
```

**Sample Response:**
```js
{
    "id": 32,
    "name": "Desmond O'Leary",
    "email": "desoleary+testing@gmail.com"
}
```

###### User Logout
```bash
curl --location --request GET 'http://localhost:5000/api/auth/signout' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMiIsImlhdCI6MTU5OTA4Mjg2NSwiZXhwIjoxNTk5Njg3NjY1fQ.Bsm4ZnmPVuejsgpCl0zT6XseMFXmcVm4GyBuIHe-x8fXJfwzA84chJDGFsoHtVcMZaGwWo_0FL031CI9fQjuIQ' \
--data-raw '{
    "email": "desoleary@gmail.com",
    "password": "some-secret-password"
}'
```

**Sample Response:**
```js
{
    "success": true,
    "message": "desoleary+testing@gmail.com logged out successfully"
}
```
