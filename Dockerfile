FROM jfisbein/docker-images:debian8-java8

RUN mkdir -p /opt/stellar-notifier/lib

COPY target/stellar-notifier-*.jar /opt/stellar-notifier
COPY target/dependency/* /opt/stellar-notifier/lib/

COPY src/main/bash/entrypoint.sh /
RUN chmod +x /entrypoint.sh

RUN wget -O /bin/smell-baron https://github.com/ohjames/smell-baron/releases/download/v0.4.2/smell-baron && chmod a+x /bin/smell-baron
ENTRYPOINT ["/bin/smell-baron"]

CMD /entrypoint.sh