# Keylogger

## Overview

This project is an **educational keylogger** application designed for security research and learning purposes. It demonstrates techniques for capturing and securely logging keyboard input on a system, with a focus on encryption and responsible usage.

> **Disclaimer:** This tool is intended strictly for educational and authorized use only. Unauthorized use of keyloggers is illegal and unethical.

## Features

- Captures keystrokes globally in real-time
- Logs data to an encrypted file using AES encryption
- User-defined encryption password
- Lightweight and efficient Java implementation
- Includes a CLI tool for secure log decryption

## Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/giorgosg1an/keylogger.git
    ```
2. **Navigate to the project directory:**
    ```bash
    cd keylogger
    ```
3. **Install dependencies:**
    - Ensure you have Java 11+ installed.
    - Download dependencies using your preferred build tool (e.g., Maven or Gradle), or manually add [JNativeHook](https://github.com/kwhat/jnativehook) to your classpath.

## Usage

### Running the Keylogger

1. **Build the project** (if using Maven):
    ```bash
    mvn clean package
    ```
2. **Run the keylogger:**
    ```bash
    java -cp target/keylogger-1.0.jar;path/to/jnativehook.jar com.github.giorgosg1an.Main
    ```
    - You will be prompted to enter an encryption password.
    - Keystrokes will be logged to `logs/keystrokes.enc` in encrypted form.
    - Press `ESC` to exit the keylogger safely.

### Decrypting the Logs

1. **Run the decryptor CLI:**
    ```bash
    java -cp target/keylogger-1.0.jar;path/to/jnativehook.jar com.github.giorgosg1an.DecryptorCLI
    ```
    - Enter the same password used during logging.
    - Decrypted keystrokes will be printed to the console.

## Project Structure

```
keylogger/
├── src/
│   └── main/
│       └── java/
│           └── com/github/giorgosg1an/
│               ├── Main.java
│               ├── KeyLogger.java
│               ├── EncryptedLogger.java
│               ├── DecryptorCLI.java
│               └── TimestampUtil.java
├── logs/
│   └── keystrokes.enc
├── README.md
└── ...
```

## Security & Ethical Notice

- **Use this software only on systems you own or have explicit permission to monitor.**
- The authors are not responsible for any misuse or illegal activity.
- All logged data is encrypted; the password is never stored.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- [JNativeHook](https://github.com/kwhat/jnativehook) for global keyboard event capture.
- Java Cryptography Architecture for secure encryption.

---
**For educational and ethical hacking