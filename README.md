# stellar-notifier

Very simple app to send email notifications when a new payment occurs to a defined Stellar account.

### Usage

#### Option A - Run as a docker container
```bash
# Example using gmail
 docker run --detach --name stellar-notifier --restart unless-stopped \
-e AccountId=GXXXXXXXXXXXXXXXXXXXXXXXXXX \
-e mail.smtp.auth=true \
-e mail.smtp.starttls.enable=true \
-e mail.smtp.host=smtp.gmail.com \
-e mail.smtp.port=587 \
-e mail.user=you@gmail.com \
-e mail.password='yourpassword' \
-e mail.recipient=alertsrecipient@yahoo.com \
 jfisbein/stellar-notifier
```

#### Option B - Compile and run directly in your computer
download the repository
```bash
git clone https://github.com/jfisbein/stellar-notifier.git
cd stellar-notifier
```

Compile the project
```bash
mvn clean package
```

Set environment values
```bash
# Example using gmail
export AccountId=GXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
export mail.smtp.auth=true
export mail.smtp.starttls.enable=true
export mail.smtp.host=smtp.gmail.com
export mail.smtp.port=587
export mail.user=you@gmail.com
export mail.password=yourpassword
export mail.recipient=alertsrecipient@yahoo.com
```

Run it
```bash
java -classpath target/dependency/*:$(ls -1 /target/stellar-notifier-*.jar | head -n1) com.sputnik.stellar.Launcher
```

