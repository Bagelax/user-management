package usermanagementbe.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseBody {
    private String error;

    public ErrorResponseBody() {
        this.error = "";
    }
}
