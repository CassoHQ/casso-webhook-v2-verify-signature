package main

import (
	"crypto/hmac"
	"crypto/sha512"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"sort"
	"strconv"
	"strings"
)

var webhookData = map[string]interface{}{
	"error": 0,
	"data": map[string]interface{}{
		"id":                     0,
		"reference":              "MA_GIAO_DICH_THU_NGHIEM",
		"description":            "giao dich thu nghiem",
		"amount":                 599000,
		"runningBalance":         25000000,
		"transactionDateTime":    "2024-10-03 15:06:37",
		"accountNumber":          "88888888",
		"bankName":               "VPBank",
		"bankAbbreviation":       "VPB",
		"virtualAccountNumber":   "",
		"virtualAccountName":     "",
		"counterAccountName":     "NGUYEN VAN A",
		"counterAccountNumber":   "8888888888",
		"counterAccountBankId":   "970415",
		"counterAccountBankName": "VietinBank",
	},
}

const checksumKey = "mgExMrjj4i7a1p8Cs6LmFbpCd8lbw4sSX4LZXPag7TKazeIogtR90tvWYTkwQbvs"

var headers = map[string]string{
	"X-Casso-Signature": "t=1727948258788,v1=ed0a4bd2e826d5cb69988cdb141e6c1a080e21f3b57eb72cd78192220042b9e7dde0868fc667faea8e224900fa7904e7c88dfa098032fb2d6b6996856e8b7ff3",
}

func sortObjDataByKey(data map[string]interface{}) map[string]interface{} {
	sortedData := make(map[string]interface{})
	keys := make([]string, 0, len(data))
	for k := range data {
		keys = append(keys, k)
	}
	sort.Strings(keys)

	for _, k := range keys {
		switch v := data[k].(type) {
		case map[string]interface{}:
			sortedData[k] = sortObjDataByKey(v)
		default:
			sortedData[k] = v
		}
	}

	return sortedData
}

func verifyWebhookSignature(headers map[string]string, data map[string]interface{}, checksumKey string) bool {
	receivedSignature := headers["X-Casso-Signature"]
	parts := strings.Split(receivedSignature, ",")
	timestampStr := strings.Split(parts[0], "=")[1]
	signature := strings.Split(parts[1], "=")[1]
	timestamp, _ := strconv.ParseInt(timestampStr, 10, 64)

	sortedData := sortObjDataByKey(data)
	messageToSign := fmt.Sprintf("%d.%s", timestamp, toJSONString(sortedData))

	h := hmac.New(sha512.New, []byte(checksumKey))
	h.Write([]byte(messageToSign))
	generatedSignature := hex.EncodeToString(h.Sum(nil))

	return signature == generatedSignature
}

func toJSONString(data map[string]interface{}) string {
	jsonBytes, _ := json.Marshal(data)
	return string(jsonBytes)
}

func main() {
	isValid := verifyWebhookSignature(headers, webhookData, checksumKey)
	fmt.Println("Signature is valid: ", isValid)
}
