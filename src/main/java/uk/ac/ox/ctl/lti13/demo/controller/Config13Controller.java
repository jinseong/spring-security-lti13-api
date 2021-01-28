package uk.ac.ox.ctl.lti13.demo.controller;

import static uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13Extension.INSTRUCTURE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nimbusds.jose.jwk.JWKSet;

import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13Extension;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13ExtensionBuilder;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13Placement;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13PlacementBuilder;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13Settings;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Canvas13SettingsBuilder;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Lti13Config;
import uk.ac.ox.ctl.lti13.demo.controller.lti13.Lti13ConfigBuilder;

@RestController
public class Config13Controller {

    @Value("${spring.application.name:LTI Tool}")
    private String title;

    @Value("${lti.application.description:Tool description.}")
    private String description;

    @Value("${lti.jwk.id:lti-jwt-id}")
    private String jwtId;

    private final JWKSet jwkSet;

    public Config13Controller(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }
    
    @Value("${publicJwk}")
    private String publicJwkString;
    
    private Object publicJwk;

    @GetMapping("/config.json")
    public Lti13Config getConfig(HttpServletRequest request) {
        String urlPrefix = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        Canvas13Placement coursePlacement = new Canvas13PlacementBuilder()
                .placement(Canvas13Placement.Placement.COURSE_NAVIGATION)
                .enabled(false)
                .messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
                .createCanvas13Placement();
        Canvas13Placement accountPlacement = new Canvas13PlacementBuilder()
                .placement(Canvas13Placement.Placement.ACCOUNT_NAVIGATION)
                .enabled(true)
                .messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
                .createCanvas13Placement();
        List<Canvas13Placement> placements = Arrays.asList(coursePlacement, accountPlacement);
        Canvas13Settings canvas13Settings = new Canvas13SettingsBuilder()
                .placements(placements)
                .createCanvas13Settings();
        Collection<Canvas13Extension> extensions  = Collections.singleton(new Canvas13ExtensionBuilder()
                .platform(INSTRUCTURE)
                .domain(request.getServerName())
                .privacyLevel(Lti13Config.PrivacyLevel.PUBLIC)
                .settings(canvas13Settings)
                .createCanvas13Extension());
        Map<String, String> customFields = new HashMap<>();
        customFields.put("canvas_css_common", "$Canvas.css.common");
        customFields.put("com_instructure_brand_config_json_url", "$com.instructure.brandConfigJS.url");
        
        try {
			JSONParser jsonParser = new JSONParser();
			publicJwk = jsonParser.parse(publicJwkString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        return new Lti13ConfigBuilder()
                .title(title)
                .description(description)
                .oidcInitiaionUrl(urlPrefix + "/lti/login_initiation/canvas")
                .targetLinkUri(urlPrefix)
                .extensions(extensions)
                .publicJwk(publicJwk)
                .customFields(customFields)
                .createLti13Config();
    }

}
