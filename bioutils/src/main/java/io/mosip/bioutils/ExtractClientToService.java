package io.mosip.bioutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.biosdk.client.impl.spec_1_0.Client_V_1_0;
import io.mosip.bioutils.dto.ExtractTemplateRequestDto;
import io.mosip.bioutils.dto.RequestDto;
import io.mosip.bioutils.utils.Util;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biosdk.provider.impl.BioProviderImpl_V_0_9;
import io.mosip.kernel.biosdk.provider.util.BIRConverter;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ExtractClientToService {
    private static final String VERSION = "1.0";
    private static Logger LOGGER = LoggerFactory.getLogger(CbeffToExtractTemplateRestRequest.class);
    private static int PRETTY_PRINT_INDENT_FACTOR = 4;
    private static final String dir = System.getProperty("user.dir");

    public static void main(String [] args) throws Exception {
        String samplePath = ExtractClientToService.class.getResource("/sample.cbeff.xml").getPath();
        String cbeffXsdPath = ExtractClientToService.class.getResource("/cbeff.xsd").getPath();
        String responseCbeffPath = Paths.get(dir, "bioutils", "output", "ExtractClientToService.cbeff.xml").toString();
        BiometricRecord sample = cbeffToBiometricRecord(samplePath);

        byte[] xsd = FileUtils.readFileToByteArray(new File(cbeffXsdPath));

        Gson gson = new GsonBuilder().serializeNulls().create();
        LOGGER.info(gson.toJson(sample));

        List<BiometricType> modalitiesToExtract = new ArrayList<BiometricType>(){{
//            add(BiometricType.FACE);
            add(BiometricType.FINGER);
//            add(BiometricType.IRIS);
        }};

        ExtractTemplateRequestDto extractTemplateRequestDto = new ExtractTemplateRequestDto();
        extractTemplateRequestDto.setSample(sample);
        extractTemplateRequestDto.setModalitiesToExtract(modalitiesToExtract);
        extractTemplateRequestDto.setFlags(new HashMap<>());

        Client_V_1_0 client_v_1_0 = new Client_V_1_0();
        Response<BiometricRecord> res = client_v_1_0.extractTemplate(sample, modalitiesToExtract, new HashMap<>());

        List<BIR> templates = new LinkedList();
        templates.add(isSuccessResponse(res) ? BIRConverter.convertToBIR((io.mosip.kernel.biometrics.entities.BIR)((BiometricRecord)res.getResponse()).getSegments().get(0)) : null);
        System.out.println(dir);
////        List<BIR> birs = gson.fromJson(gson.toJson(res.getResponse().getSegments()), List.class);
        CbeffImpl cbeff = new CbeffImpl();
        byte[] cbe_bytes = cbeff.createXML(templates, xsd);
        FileUtils.writeByteArrayToFile(new File(responseCbeffPath), cbe_bytes);
//        String requstJson = gson.toJson(res);
//        LOGGER.info(requstJson);
    }

    private static boolean isSuccessResponse(Response<?> response) {
        return response != null && response.getStatusCode() >= 200 && response.getStatusCode() <= 299 && response.getResponse() != null;
    }

    private static BiometricRecord cbeffToBiometricRecord(String filePath) throws Exception {
        CbeffImpl cbeff = new CbeffImpl();
        List<BIRType> birType = cbeff.getBIRDataFromXML(FileUtils.readFileToByteArray(new File(filePath)));
        List<BIR> cb_birs = cbeff.convertBIRTypeToBIR(birType);
        BiometricRecord biometricRecord = getBiometricRecord(cb_birs.toArray(new BIR[cb_birs.size()]));
        return biometricRecord;
    }

    private static BiometricRecord getBiometricRecord(BIR[] birs) {
        BiometricRecord biometricRecord = new BiometricRecord();
        biometricRecord.setSegments(new LinkedList<>());
        for (int i = 0; i < birs.length; i++) {
            biometricRecord.getSegments().add(BIRConverter.convertToBiometricRecordBIR(birs[i]));
        }
        return biometricRecord;
    }
}
