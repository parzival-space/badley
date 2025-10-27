package space.parzival.discord.badley.configuration.properties.behavior;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "badley.ai.behavior")
@AllArgsConstructor
public class RandomReplyProperties {
    /**
     * Whether the Ai should randomly reply to messages even though it was not addressed directly.
     * Also known as the 'Who asked?' Feature
     */
    private boolean enabled;

    /**
     * The chance in percentage for when the Ai should respond without a mention.
     */
    @Min((long) 0)
    @Max((long) 1)
    @NotNull
    private double chance = 0.01;
}
