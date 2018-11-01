# Likensing server

This is a small server for generating licenses which can be used by the [Likensing Client](https://github.com/fkrone/likensing-client). It is just a little fun project. Feel free to fork and improve it.

# Running the server
A MySQL database is needed (MySQL 5.7 is tested).
To get started, create a file named ```instance.conf``` in the root directory with the following content:
```
licensing {
    privateKey = "INSERT YOUR PRIVATE KEY HERE"
}

jdbc {
    database {
        system {
            profile = "mysql"
            user = "USERNAME"
            password = "PASSWORD"
            database = "MYSQL_DATABASE_NAME"
        }
    }
}

http.port=PORTNUMBER
http.sessionSecret=RANDOM_SECRET
```

After that, start the server and log in with
```
user: system
password: system
```

Navigate to the console on the right dropdown and execute the command ```generateKeys``` to generate your public private key pair.

To run tests, Docker has to be installed.

# License

Likensing Server is licensed under the MIT License.