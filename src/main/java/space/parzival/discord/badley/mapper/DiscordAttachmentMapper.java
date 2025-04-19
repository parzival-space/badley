package space.parzival.discord.badley.mapper;

import net.dv8tion.jda.api.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.ai.content.Media;

@Mapper(componentModel = "spring")
public interface DiscordAttachmentMapper {

    @Mapping(target = "id", source = "attachment.id")
    @Mapping(target = "name", source = "attachment.fileName")
    @Mapping(target = "mimeType", expression = "java(org.springframework.util.MimeType.valueOf(attachment.getContentType()))")
    @Mapping(target = "data", source = "attachment.url")
    Media mapToMedia(Message.Attachment attachment);

    default String mapToInvalidMediaString(Message.Attachment attachment) {
        return String.format("<invalid_attachment id='%s' size=%d type='%s'>%s</invalid_attachment>",
                attachment.getId(),
                attachment.getSize(),
                attachment.getContentType(),
                attachment.getFileName());
    }
}
