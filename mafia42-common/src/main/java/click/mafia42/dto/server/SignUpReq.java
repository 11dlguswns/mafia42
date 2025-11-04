package click.mafia42.dto.server;

import jakarta.validation.constraints.Size;

public record SignUpReq(
        @Size(min = 1, max = 6, message = "이름은 6글자 이하여야 합니다.")
        String nickname,
        String password
) {
}
