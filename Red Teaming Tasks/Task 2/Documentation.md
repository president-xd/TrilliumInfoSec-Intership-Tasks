# <span style="color:red;"> Insecure AES Encryption in Mobile Apps | Unveiling the Vulnerability </span>
## <span style="color:blue;">Hello Guys,</span>

In this blog, we’ll walk you through our journey, what we discovered, and the lessons learned along the way.

## <span style="color:blue;">Introduction</span>

In today’s mobile-centric world, securing sensitive data on our devices is crucial. Encryption is a go-to method for protecting this information, but even the best intentions can fall short if the implementation isn’t done correctly. Recently, we took on the challenge of developing a Kotlin app that encrypts usernames and passwords using AES encryption. What seemed like a straightforward task quickly revealed a significant security flaw, exposing the very credentials we aimed to protect. So let's move further.

## <span style="color:blue;">Key Points</span>

- **Encryption Method:** AES (Advanced Encryption Standard)
- **Storage Location:** `/Downloads` folder (In my case, or it could be any location in a mobile or system)
- **Key and IV Generation:** Provided in source code

## <span style="color:blue;">Implementation Overview</span>

### <span style="color:blue;">Step 1: Developing the Kotlin App</span>

We started by building and installing the app on a mobile device. The app was designed to capture the username and password, encrypt them using AES encryption, and store the encrypted data in the `/Downloads` folder.

**Application interface:**

![image](https://github.com/user-attachments/assets/0195fd10-1681-4abe-9011-734dabf824f7)


### <span style="color:blue;">Step 2: Encryption and Storage</span>

Once the app was up and running, it encrypted the credentials using AES and saved the encrypted data in the `/Downloads` directory. However, the problem lay in how the encryption mechanism and keys were managed.

### <span style="color:blue;">Step 3: Analyzing the Encryption</span>

After installing the app, we decided to reverse-engineer the APK file using JADX to take a closer look at its functionality. What we found was alarming: the encryption method, key, and IV were all hardcoded in the app’s code. This exposed a major security vulnerability.

## <span style="color:blue;">The Vulnerability: Insecure Storage and Key Management</span>

### <span style="color:blue;">How It Unfolded</span>

When we dug deeper into the encrypted credentials, we discovered that the encryption key and IV were not only accessible but also embedded within the app’s source code. This meant that anyone with the APK file and some reverse-engineering skills could easily extract the key and IV.

To demonstrate this vulnerability, we wrote a script that accessed the `/Downloads` folder, decrypted the credentials using the extracted key and IV, and retrieved the plaintext username and password.

**Screenshot and Evidence**
![image](https://github.com/user-attachments/assets/dc95b2df-e960-42e6-b877-7c606cdf6d73)

![image](https://github.com/user-attachments/assets/e6d935bc-0f26-48ca-95ed-8a7abce590cf)



## <span style="color:blue;">Getting the Credentials</span>

If you got the encryption mechanism, also the file location where the credentials are stored, then it would be easy to decrypt them, and it can be used in an unethical way.

I used a script to decrypt and print the data. Here is an example:

![image](https://github.com/user-attachments/assets/a183bcbc-7608-46a7-a55a-e812ad5c8e32)


## <span style="color:blue;">Moral of the Story</span>

This vulnerability underscores the critical need to secure encryption keys and initialization vectors. Storing these sensitive elements within the application code or alongside encrypted data can render the encryption useless. Here are some best practices to follow:

- **Avoid Hardcoding Keys:** Never embed encryption keys or IVs in your source code. (In our case, the mechanism of encryption was revealed)
- **Use Secure Storage:** Utilize secure storage mechanisms provided by the operating system.
- **Implement Proper Access Controls:** Ensure that sensitive data and keys are only accessible to authorized entities.

## <span style="color:blue;">Conclusion</span>

Insecure encryption practices can lead to severe data breaches, exposing sensitive information despite encryption efforts. By understanding and addressing the flaws in our implementation, we can better safeguard user credentials and improve overall security. Remember, encryption is only as strong as its weakest link.


