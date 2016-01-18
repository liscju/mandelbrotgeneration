# Building app locally

Install rabbitmq:
http://arcware.net/installing-rabbitmq-on-windows/

Run rabbitmq:

On windows:
$ rabbitmq-server.bat install

On unix
$ rabbitmq-server.sh install

Add user test with password test in rabbitmq-server, describe in:
http://stackoverflow.com/questions/26811924/spring-amqp-rabbitmq-3-3-5-access-refused-login-was-refused-using-authentica

Install postgresql, add user test with password test, add user all privileges(it will be easier
not to deal with them :P), add mandelbrot database

Build application:
$ mvn clean install

# Running app locally:

Unix:
$ run_app.sh

Windows:
$ run_app.bat

# Deploying app:

$ heroku create

$ git push heroku master

$ heroku addons:create cloudamqp:lemur

$ heroku open

# Existing instances:

https://mandelbrotgeneration.herokuapp.com/mandelbrot