<?php

$webhookData = [
    "error" => 0,
    "data" => [
        "id" => 0,
        "reference" => "MA_GIAO_DICH_THU_NGHIEM",
        "description" => "giao dich thu nghiem",
        "amount" => 599000,
        "runningBalance" => 25000000,
        "transactionDateTime" => "2024-10-03 15:06:37",
        "accountNumber" => "88888888",
        "bankName" => "VPBank",
        "bankAbbreviation" => "VPB",
        "virtualAccountNumber" => "",
        "virtualAccountName" => "",
        "counterAccountName" => "NGUYEN VAN A",
        "counterAccountNumber" => "8888888888",
        "counterAccountBankId" => "970415",
        "counterAccountBankName" => "VietinBank"
    ]
];

$checksumKey = 'mgExMrjj4i7a1p8Cs6LmFbpCd8lbw4sSX4LZXPag7TKazeIogtR90tvWYTkwQbvs';

$headers = [
    "X-Casso-Signature" => "t=1727948258788,v1=ed0a4bd2e826d5cb69988cdb141e6c1a080e21f3b57eb72cd78192220042b9e7dde0868fc667faea8e224900fa7904e7c88dfa098032fb2d6b6996856e8b7ff3"
];

function sortObjDataByKey($data) {
    $sortedObj = [];
    $keys = array_keys($data);
    sort($keys);
    foreach ($keys as $key) {
        if (is_array($data[$key])) {
            $sortedObj[$key] = sortObjDataByKey($data[$key]);
        } else {
            $sortedObj[$key] = $data[$key];
        }
    }
    return $sortedObj;
}

function verifyWebhookSignature($headers, $data, $checksumKey) {
    $receivedSignature = $headers["X-Casso-Signature"];
    preg_match('/t=(\d+),v1=([a-f0-9]+)/', $receivedSignature, $matches);
    $timestampStr = $matches[1] ?? null;
    $signature = $matches[2] ?? null;
    $timestamp = (int)$timestampStr;

    $sortedDataByKey = sortObjDataByKey($data);
    $messageToSign = $timestamp . "." . json_encode($sortedDataByKey);
    $generatedSignature = hash_hmac("sha512", $messageToSign, $checksumKey);

    return $signature === $generatedSignature;
}

$isValid = verifyWebhookSignature($headers, $webhookData, $checksumKey);