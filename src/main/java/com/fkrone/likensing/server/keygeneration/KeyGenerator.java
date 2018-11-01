package com.fkrone.likensing.server.keygeneration;

import com.google.common.io.BaseEncoding;
import sirius.kernel.commons.Values;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.Exceptions;
import sirius.kernel.health.console.Command;

import javax.annotation.Nonnull;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Command to generate a private public key pair for generating licenses.
 */
@Register(classes = Command.class)
public class KeyGenerator implements Command {

    @Override
    public void execute(Output output, String... strings) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(Values.of(strings).at(0).asInt(2048));

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String privateKey = BaseEncoding.base64().encode(keyPair.getPrivate().getEncoded());
            output.line("This is your private key. Keep it SECRET and DO NOT SHARE it.");
            output.line(privateKey);

            output.blankLine();
            output.separator();
            output.blankLine();

            String publicKey = BaseEncoding.base64().encode(keyPair.getPublic().getEncoded());
            output.line("This is your public key. You can ship it with your software.");
            output.line(publicKey);
            output.blankLine();
        } catch (Exception e) {
            output.line(Exceptions.handle(e).getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Generates a pair of public and private key for generating licenses";
    }

    @Nonnull
    @Override
    public String getName() {
        return "generateKeys";
    }
}
