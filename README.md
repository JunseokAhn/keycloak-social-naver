# Naver social identity providers for Keycloak.

This is Naver social identity providers library for [Keycloak](https://www.keycloak.org/) server.
With this library you can log in into Keycloak via [Naver](https://www.naver.com)

## Keycloak versions

It was tested against Keycloak versions:
+ 18.0.0

## How to use it

To install this library manually, you could follow [instruction](https://www.keycloak.org/docs/latest/server_development/index.html#registering-provider-implementations) with a few extra steps.

Here's steps:

1. Build project from source or download release file.
2. Copy `keycloak-social-naver-1.0.0.jar` to `KEYCLOAK_HOME/providers`.
3. Add files from `src/main/resources/theme` to `${keycloak.home.dir}/lib/lib/main/org.keycloak.keycloak-themes-18.0.0.jar`

