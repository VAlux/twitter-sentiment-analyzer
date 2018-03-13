FROM rabbitmq:management-alpine
ADD rabbitmq.conf /etc/rabbitmq/
ADD definitions.json /etc/rabbitmq/
RUN chown rabbitmq:rabbitmq /etc/rabbitmq/rabbitmq.conf /etc/rabbitmq/definitions.json
RUN rabbitmq-plugins enable rabbitmq_web_stomp
EXPOSE 5672 15672 15674
CMD ["rabbitmq-server"]