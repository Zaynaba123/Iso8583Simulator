package com.example.isoserveurservice.iso;

import lombok.extern.slf4j.Slf4j;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.*;
import org.jpos.q2.Q2;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.NameRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin("*")
public class SwitchRequestListener implements ISORequestListener, Configurable {

    @Bean
    public Q2 q2() {
        Q2 q2 = new Q2();
        q2.start();
        return q2;
    }

    private Map<String, String> routeTable;
    private RequestResponseProvider requestResponseProvider;
    private MUX mux;

    @Override
    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        routeTable = new HashMap<>();
        routeTable.put("8001", "server_1");
        routeTable.put("8002", "server_2");

        String caseFilePath = configuration.get("CardCfg/card.xml");
        String ecodeFilePath = configuration.get("CardCfg/FieldEcodes.xml");
        requestResponseProvider = new RequestResponseProvider(caseFilePath, ecodeFilePath);
    }

    @Override
    public boolean process(ISOSource isoSource, ISOMsg isoMsg) {
        try {
            if (mux == null) {
                mux = QMUX.getMUX("server_1-mux");
            }

            // Log the MTI of the received message
            String mti = isoMsg.getMTI();
            log.info("Received MTI: {}", mti);

            // Check if the MTI is a request type (e.g., 0200)
            if (!"0100".equals(mti)) {
                log.error("Received message is not a request. MTI: {}", mti);
                return false;
            }

            Map<String, String> requestFields = new HashMap<>();
            for (int i = 0; i <= 127; i++) { // Updated to handle fields from 0 to 127
                if (isoMsg.hasField(i)) {
                    requestFields.put(String.valueOf(i), isoMsg.getString(i));
                }
            }

            log.info("Request fields: {}", requestFields);

            Map<String, String> responseFields = requestResponseProvider.getResponseForRequest(requestFields);

            ISOMsg reply = (ISOMsg) isoMsg.clone();
            reply.setResponseMTI();

            if (responseFields != null) {
                log.info("Response fields found: {}", responseFields);
                for (Map.Entry<String, String> entry : responseFields.entrySet()) {
                    reply.set(Integer.parseInt(entry.getKey()), entry.getValue());
                }
            } else {
                log.info("No matching response found. Setting default response code 00.");
                reply.set(39, "00"); // Default response code
            }

            // Apply the logic to filter fields based on ecode
            Map<Integer, Integer> fieldEcodes = requestResponseProvider.getFieldEcodes();
            for (int i = 0; i <= 127; i++) { // Updated to handle fields from 0 to 127
                if (reply.hasField(i) && fieldEcodes.getOrDefault(i, 0) == 0) {
                    reply.unset(i);
                }
            }



            reply.set(60, "server");

            isoSource.send(reply);

            return true;

        } catch (ISOException | IOException | NameRegistrar.NotFoundException e) {
            log.error("Error processing request", e);
        }
        return false;
    }
}
