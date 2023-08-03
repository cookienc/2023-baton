package touch.baton.domain.oauth.client;

import org.springframework.stereotype.Component;
import touch.baton.domain.common.exception.ClientErrorCode;
import touch.baton.domain.oauth.OauthInformation;
import touch.baton.domain.oauth.OauthType;
import touch.baton.domain.oauth.exception.OauthRequestException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Component
public class OauthInformationClientComposite {

    private final Map<OauthType, OauthInformationClient> clients;

    public OauthInformationClientComposite(final Set<OauthInformationClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toMap(
                        OauthInformationClient::oauthType,
                        identity()
                ));
    }

    public OauthInformation fetchInformation(final OauthType oauthType, final String authCode) {
        return Optional.ofNullable(clients.get(oauthType))
                .orElseThrow(() -> new OauthRequestException(ClientErrorCode.OAUTH_INFORMATION_CLIENT_IS_WRONG))
                .fetchInformation(authCode);
    }
}
