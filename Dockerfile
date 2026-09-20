FROM debian:bookworm-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    maven \
    openjdk-11-jdk-headless \
    chromium \
    chromium-driver \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/bin/chromium
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY . .

CMD ["mvn", "-B", "test"]