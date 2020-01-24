package eu.kvfg.refresh.constant;

import lombok.Getter;
import org.springframework.boot.info.GitProperties;

import java.time.Instant;

/**
 * @author Lukas Nasarek
 */
class VersionInformation {

    @Getter
    private final String branch;

    @Getter
    private final String id;

    @Getter
    private final String shortId;

    @Getter
    private final Instant date;

    private VersionInformation(String branch, String id, String shortId, Instant date) {
        this.branch = branch;
        this.id = id;
        this.shortId = shortId;
        this.date = date;
    }

    /**
     * Creates a new {@link VersionInformation} object which contains the
     * basic version information hold by the {@link GitProperties}.
     *
     * @param gitProperties contains the version information
     * @return a new {@link VersionInformation} object which contains
     * the basic version information
     */
    public static VersionInformation of(GitProperties gitProperties) {
        return new VersionInformation(gitProperties.getBranch(),
            gitProperties.getCommitId(),
            gitProperties.getShortCommitId(),
            Instant.ofEpochMilli(gitProperties.getCommitTime().toEpochMilli()));
    }
}
