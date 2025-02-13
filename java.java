import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebhookVerifier {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> sortObjDataByKey(Map<String, Object> data) {
        Map<String, Object> sortedMap = new TreeMap<>();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof Map) {
                sortedMap.put(key, sortObjDataByKey((Map<String, Object>) value));
            } else {
                sortedMap.put(key, value);
            }
        }
        return sortedMap;
    }

    public static boolean verifyWebhookSignature(Map<String, String> headers, Map<String, Object> data, String checksumKey) throws Exception {
        String receivedSignature = headers.get("X-Casso-Signature");
        String[] signatureParts = receivedSignature.split(",");
        String timestampStr = signatureParts[0].split("=")[1];
        String signature = signatureParts[1].split("=")[1];
        long timestamp = Long.parseLong(timestampStr);

        Map<String, Object> sortedDataByKey = sortObjDataByKey(data);
        String messageToSign = timestamp + "." + objectMapper.writeValueAsString(sortedDataByKey);
        
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(secretKeySpec);
        byte[] hash = hmacSha512.doFinal(messageToSign.getBytes(StandardCharsets.UTF_8));
        StringBuilder generatedSignature = new StringBuilder();
        for (byte b : hash) {
            generatedSignature.append(String.format("%02x", b));
        }

        return signature.equals(generatedSignature.toString());
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> webhookData = new HashMap<>();
        webhookData.put("error", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("reference", "MA_GIAO_DICH_THU_NGHIEM");
        data.put("description", "giao dich thu nghiem");
        data.put("amount", 599000);
        data.put("runningBalance", 25000000);
        data.put("transactionDateTime", "2024-10-03 15:06:37");
        data.put("accountNumber", "88888888");
        data.put("bankName", "VPBank");
        data.put("bankAbbreviation", "VPB");
        data.put("virtualAccountNumber", "");
        data.put("virtualAccountName", "");
        data.put("counterAccountName", "NGUYEN VAN A");
        data.put("counterAccountNumber", "8888888888");
        data.put("counterAccountBankId", "970415");
        data.put("counterAccountBankName", "VietinBank");
        webhookData.put("data", data);

        String checksumKey = "mgExMrjj4i7a1p8Cs6LmFbpCd8lbw4sSX4LZXPag7TKazeIogtR90tvWYTkwQbvs";

        Map<String, String> headers = Map.of(
            "X-Casso-Signature", "t=1727948258788,v1=ed0a4bd2e826d5cb69988cdb141e6c1a080e21f3b57eb72cd78192220042b9e7dde0868fc667faea8e224900fa7904e7c88dfa098032fb2d6b6996856e8b7ff3"
        );

        boolean isValid = verifyWebhookSignature(headers, webhookData, checksumKey);
        System.out.println("Signature is valid: " + isValid);
    }
}