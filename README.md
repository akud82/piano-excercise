## 1. First you need install webpack if not installed yet.

```
$ sudo npm install webpack -g
```

## 2. Next build web parts

```
$ cd src/main/js
$ npm install
$ npm run build
```

## 3. And run spring-boot server

```
$ clean spring-boot:run -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=dev
```

## 4. Show the application

```
open http://localhost:8080
```