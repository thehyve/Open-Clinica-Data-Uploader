package nl.thehyve.ocdu.autonomous;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class which is responsible for loading {@link UploadChannelConfig} classes from the file system.
 * Created by Jacob Rousseau on 21-Jul-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class UploadChannelConfigLoader {

    public static UploadChannelConfig load(String path) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = Files.newInputStream( Paths.get(path));
        return yaml.loadAs(inputStream, UploadChannelConfig.class);
    }
}
