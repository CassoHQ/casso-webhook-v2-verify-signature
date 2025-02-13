using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using Newtonsoft.Json;

class Program
{
    static void Main()
    {
        var webhookData = new Dictionary<string, object>
        {
            { "error", 0 },
            { "data", new Dictionary<string, object>
                {
                    { "id", 0 },
                    { "reference", "MA_GIAO_DICH_THU_NGHIEM" },
                    { "description", "giao dich thu nghiem" },
                    { "amount", 599000 },
                    { "runningBalance", 25000000 },
                    { "transactionDateTime", "2024-10-03 15:06:37" },
                    { "accountNumber", "88888888" },
                    { "bankName", "VPBank" },
                    { "bankAbbreviation", "VPB" },
                    { "virtualAccountNumber", "" },
                    { "virtualAccountName", "" },
                    { "counterAccountName", "NGUYEN VAN A" },
                    { "counterAccountNumber", "8888888888" },
                    { "counterAccountBankId", "970415" },
                    { "counterAccountBankName", "VietinBank" }
                }
            }
        };

        string checksumKey = "mgExMrjj4i7a1p8Cs6LmFbpCd8lbw4sSX4LZXPag7TKazeIogtR90tvWYTkwQbvs";
        var headers = new Dictionary<string, string>
        {
            { "X-Casso-Signature", "t=1727948258788,v1=ed0a4bd2e826d5cb69988cdb141e6c1a080e21f3b57eb72cd78192220042b9e7dde0868fc667faea8e224900fa7904e7c88dfa098032fb2d6b6996856e8b7ff3" }
        };

        var isValid = VerifyWebhookSignature(headers, webhookData, checksumKey);
    }

    static Dictionary<string, object> SortObjDataByKey(Dictionary<string, object> data)
    {
        var sortedDict = new Dictionary<string, object>();

        foreach (var item in data.OrderBy(x => x.Key)) {
            if (item.Value is Dictionary<string, object> nestedDict) {
                sortedDict[item.Key] = SortObjDataByKey(nestedDict);
            } else {
                sortedDict[item.Key] = item.Value;
            }
        }

        return sortedDict;
    }


    static bool VerifyWebhookSignature(Dictionary<string, string> headers, Dictionary<string, object> data, string checksumKey)
    {
        string receivedSignature = headers["X-Casso-Signature"];
        var match = System.Text.RegularExpressions.Regex.Match(receivedSignature, @"t=(\d+),v1=([a-f0-9]+)");
        string timestampStr = match.Groups[1].Value;
        string signature = match.Groups[2].Value;
        long timestamp = long.Parse(timestampStr);

        var sortedDataByKey = SortObjDataByKey(data);
        string messageToSign = timestamp + "." + JsonConvert.SerializeObject(sortedDataByKey);
        string generatedSignature = CreateHmacSHA512(checksumKey, messageToSign);

        return signature == generatedSignature;
    }

    static string CreateHmacSHA512(string key, string message)
    {
        using (var hmac = new HMACSHA512(Encoding.UTF8.GetBytes(key)))
        {
            byte[] hash = hmac.ComputeHash(Encoding.UTF8.GetBytes(message));
            return BitConverter.ToString(hash).Replace("-", "").ToLower();
        }
    }
}