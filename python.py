import hashlib
import json
import hmac

webhook_data = {
    "error": 0,
    "data": {
        "id": 0,
        "reference": "MA_GIAO_DICH_THU_NGHIEM",
        "description": "giao dich thu nghiem",
        "amount": 599000,
        "runningBalance": 25000000,
        "transactionDateTime": "2024-10-03 15:06:37",
        "accountNumber": "88888888",
        "bankName": "VPBank",
        "bankAbbreviation": "VPB",
        "virtualAccountNumber": "",
        "virtualAccountName": "",
        "counterAccountName": "NGUYEN VAN A",
        "counterAccountNumber": "8888888888",
        "counterAccountBankId": "970415",
        "counterAccountBankName": "VietinBank"
    }
}

checksum_key = 'mgExMrjj4i7a1p8Cs6LmFbpCd8lbw4sSX4LZXPag7TKazeIogtR90tvWYTkwQbvs'

headers = {
    "X-Casso-Signature": "t=1727948258788,v1=ed0a4bd2e826d5cb69988cdb141e6c1a080e21f3b57eb72cd78192220042b9e7dde0868fc667faea8e224900fa7904e7c88dfa098032fb2d6b6996856e8b7ff3"
}

def sort_obj_data_by_key(data):
    sorted_obj = {}
    for key in sorted(data.keys()):
        if isinstance(data[key], dict):
            sorted_obj[key] = sort_obj_data_by_key(data[key])
        else:
            sorted_obj[key] = data[key]
    return sorted_obj

def verify_webhook_signature(headers, data, checksum_key):
    received_signature = headers["X-Casso-Signature"]
    timestamp_str, signature = received_signature.split(",")[0][2:], received_signature.split(",")[1][3:]
    timestamp = int(timestamp_str)

    sorted_data_by_key = sort_obj_data_by_key(data)
    message_to_sign = f"{timestamp}.{json.dumps(sorted_data_by_key, separators=(',', ':'))}"

    generated_signature = hmac.new(
        checksum_key.encode('utf-8'),
        message_to_sign.encode('utf-8'),
        hashlib.sha512
    ).hexdigest()
    return signature == generated_signature

isValid = verify_webhook_signature(headers, webhook_data, checksum_key)