const crypto = require("crypto");

const webhookData = {
    "error": 0,
    "data": {
      "id": 218897,
      "reference": "FT24364030863634",
      "description": "hoi lai 100 bao mun dua",
      "amount": 16775000,
      "runningBalance": 16775000,
      "transactionDateTime": "2024-12-23 07:00:00",
      "accountNumber": "123456789",
      "bankName": "MBBank",
      "bankAbbreviation": "MBB",
      "virtualAccountNumber": "",
      "virtualAccountName": "",
      "counterAccountName": "",
      "counterAccountNumber": "",
      "counterAccountBankId": "",
      "counterAccountBankName": ""
    }
  }

const checksumKey = 'g3oZ950pJQ4k6REhOPGkx37RsXgWz9QJ9RCAZ7i0yagLF32XQZtemQ6r3JIo4MCr';

const headers = {
    "X-Casso-Signature": "t=1734924830020,v1=6cec920aa3352341d3710d4ce89de3c73481739bdf240c89a440fb988bfb113f87be23dc75ad16982f6fbdb65553b73f4e51e73ed4764928f0401cd4a949a4c8"
};

function sortObjDataByKey(data) {
        const sortedObj = {};
        Object.keys(data).sort().forEach((key) => {
            if (typeof data[key] === 'object') {
                sortedObj[key] = sortObjDataByKey(data[key]);
            } else {
                sortedObj[key] = data[key];
            }
        });
        return sortedObj;
}

function verifyWebhookSignature(headers, data, checksumKey) {
    const receivedSignature = headers["X-Casso-Signature"];
    const [, timestampStr, signature] = receivedSignature.match(/t=(\d+),v1=([a-f0-9]+)/) || [];
    const timestamp = parseInt(timestampStr, 10);
    
    const sortedDataByKey = sortObjDataByKey(data);
    const messageToSign = timestamp + "." + JSON.stringify(sortedDataByKey);
    const generatedSignature = crypto.createHmac("sha512", checksumKey).update(messageToSign).digest("hex");
    console.log({signature, generatedSignature});
    return signature === generatedSignature;
}

const isValid = verifyWebhookSignature(headers, webhookData, checksumKey);
console.log(isValid);