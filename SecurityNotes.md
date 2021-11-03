# Security Configuration guid

## Введение

Данный гайд описывает, как вы можете конфигурировать доступ к своим енд-пойнтам.

### Важное
* Пожалуйста, конфигурируйте данный файл последовательно. То есть не нужно смешивать енд-пойнты разных сервисов между собой.
  Делайте все структурировано.
* Каждый енд-пойнт имеет разрешение `ACCESS`. Лишь некоторые должны быть доступны неавторизованному пользователю.

## Разрешения пользователя

В нашей реализации есть несколько разрешений, которые пользователь может иметь:

* `ACCESS` - стандартное разрешение авторизованного пользователя.
* `REFRESH` - разрешение, дающее право обновить `access token`.
* `USER` - стандартное разрешение пользователя.
* `ADMIN` - стандартное разрешение админа.

## Конфигурация файла SecurityConfiguration.kt

В
файле [`SecurityConfiguration.kt`](https://github.com/shchuko/demo-service-181/blob/11e42ed74fc3840dba9b0fe8f272c2ac56ada19b/src/main/kotlin/com/itmo/microservices/shop/common/security/SecurityConfiguration.kt)
вы можете конфигурировать доступ к своим енд-пойнтам.

Чтобы добавить доступ к определенному енд-пойнту вы должны внести изменения в следующую функцию:
```kotlin
override fun configure(http: HttpSecurity) {
    http
        .cors().configurationSource {
            CorsConfiguration()
                .also { it.allowedOrigins = listOf("*") }
                .also { it.allowedHeaders = listOf("*") }
                .also { it.allowedMethods = listOf("*") }
        }.and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/users").permitAll()
        .antMatchers(HttpMethod.POST, "/users/authentication").permitAll()
        .antMatchers(HttpMethod.POST, "/users/refresh").hasAuthority("REFRESH")
        .antMatchers("/actuator/**").permitAll()
        .antMatchers("/h2-console/**").permitAll()
        .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
        .antMatchers(HttpMethod.OPTIONS).permitAll()
        .anyRequest().hasAuthority("ACCESS")
        .and()
        .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .addFilterAfter(refreshAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        .headers().frameOptions().sameOrigin()
}
```

### Примеры

* К примеру добавим к енд-пойнту `GET /user/{user_id}` право `ADMIN`. Для этого добавим следующую строчку:
```kotlin
...
// after first function add()
.antMatchers(HttpMethod.GET, "/users/{user_id}").hasAuthority("ADMIN")
//before second function add()
...
```
* К примеру добавим к енд-пойнту `POST /orders/{oreder_id}` право `USER`. Для этого добавим следующую строчку:
```kotlin
...
// after first function add()
.antMatchers(HttpMethod.POST, "/orders/{order_id}").hasAuthority("USER")
//before second function add()
...
```
* Если вы хотите применить разрешение (к примеру `ACCESS`) ко всем запросам, можете написать следующую строчку:
```kotlin
...
// after first function add()
.anyRequest().hasAuthority("ACCESS")
//before second function add()
...
```
* Если вы хотите, чтобы неавторизованный пользователь смог получить доступ к енд-пойнту, можете написать следующую строчку:
```kotlin
...
// after first function add()
.antMatchers(HttpMethod.POST, "/orders/{order_id}").permitAll()
//before second function add()
...
```