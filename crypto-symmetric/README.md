# Symmetric Crypto

Utility for symmetric encryption.

Usually used in combination with [``client-storage``](../client-storage/) to encrypt data stored on the client.

## Default providers

### ChaCha20

_Recommended_

Use [ChaCha20](https://en.wikipedia.org/wiki/Salsa20#ChaCha_variant) to encrypt the data.

Does not utilize message authentication (decrypted and encrypted content have the same size/length) and is therefore more compact as AES/GCM.

[Better performance then AES/GCM](https://en.wikipedia.org/wiki/Galois/Counter_Mode#Performance).

### AES/GCM (legacy)

Uses AES/GCM/NoPadding.

Encrypted content is a bit bigger.
