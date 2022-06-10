import {SHA256} from 'crypto-js'
import JsEncrypt from 'jsencrypt'

export const RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB"

// 统一的 password加密
export function PasswordRSAEncrypt(
    password: string | undefined,
    rsaPublicKey: string = RSA_PUBLIC_KEY,
    date: Date = new Date()
) {
    if (!password) {
        return undefined
    }
    return RSAEncryptPro(
        SHA256(SHA256(password).toString()).toString(),
        rsaPublicKey,
        date
    )
}

// 非对称加密：增强版，加入时间戳
export function RSAEncryptPro(
    word: string | undefined,
    rsaPublicKey: string = RSA_PUBLIC_KEY,
    date: Date = new Date()
) {
    if (!word) {
        return undefined
    }
    const timestamp = ';' + date.setMinutes(date.getMinutes() + 1) // 时间戳：一分钟
    const rsaEncrypt = RSAEncrypt(word + timestamp, rsaPublicKey); // 加入时间戳，进行非对称加密
    return rsaEncrypt ? rsaEncrypt : undefined
}

// 非对称加密
function RSAEncrypt(word: string | undefined, rsaPublicKey: string) {
    if (!word) {
        return undefined
    }
    const jse = new JsEncrypt()
    jse.setPublicKey(rsaPublicKey) // 设置公钥
    return jse.encrypt(word)
}
