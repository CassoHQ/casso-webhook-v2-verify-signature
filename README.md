# Sample code hướng dẫn tạo và kiểm tra chữ ký số sử dụng trong Tích hợp Webhook V2
Casso cung cấp code hướng dẫn được hiện thực bằng các ngôn ngữ lập trình phổ biến hiện nay như Java, C#, JavaScript, PHP, Python, Golang. 
## Quy trình tạo và kiểm tra chữ ký số

1. Truy xuất giá trị của header có tên là X-Casso-Signature được gắn vào request mà Casso gửi tới hệ thống của bạn. Giá trị này có 2 thành phần cách nhau bởi dấu phẩy (,):
* t là giá trị timestamp tại thời điểm tạo chữ ký số ở hệ thống của Casso
* v1 là chữ ký số

    Ví dụ: t=1734924830020,v1=6cec920aa3352341d3710d4ce89de3c73481739bdf240c89a440fb988bfb113f87be23dc75ad16982f6fbdb65553b73f4e51e73ed4764928f0401cd4a949a4c8
2. Xử lý bóc tách giá trị ở Bước 1 thu được 2 giá trị t, v1
3. Sắp xếp lại dữ liệu webhook (webhook data) theo thứ tự tăng dần (A -> Z) key của Object data.
4. Chuyển dữ liệu webhook về dạng Chuỗi JSON
5. Tạo dữ liệu để chuẩn bị ký số theo format: `t + "." + Chuỗi JSON đã tạo ở Bước 4`
6. Tạo chữ ký số với dữ liệu ở Bước 5, key tạo chữ ký số là Key bảo mật đã được tạo lúc Tạo Tích hợp Webhook V2 ở giao diện Casso với thuật toán SHA-512 và mã hóa ở dạng Hex.
7. So sánh chữ ký số vừa tạo có trùng khớp với giá trị v1 đã thu được ở Bước 1 không. Nếu trùng khớp, có nghĩa là dữ liệu được gửi qua Webhook đảm bảo toàn vẹn. Nếu không, hãy cẩn thận, dữ liệu này có thể đã bị thay đổi trong quá trình Casso gửi dữ liệu cho tới khi hệ thống của bạn nhận nó, hoặc có thể nó không được gửi từ Casso.

> [!WARNING]  
> Hãy lưu Key bảo mật ở nơi an toàn. Nếu Key bảo mật bị rò rỉ, việc xác thực này có thể sẽ không còn ý nghĩa, bạn sẽ phải quay lại Tích hợp Webhook V2 và tạo lại một Key bảo mật mới đồng thời lưu nó vào một nơi khác.

## Các thành phần trong mỗi file code sample
1. `webhook_data (webhookData)`: Dữ liệu giao dịch gửi qua webhook
2. `checksum_key (checksumKey)`: Key bảo mật của tích hợp Webhook V2
3. `headers`: Giả lập việc chữ ký số gửi kèm webhook sẽ được gắn vào request headers với key là X-Casso-Signature.
4. `sort_obj_data_by_key (sortObjDataByKey)`: hàm/phương thức sắp xếp dữ liệu theo thứ tự tăng dần (A -> Z) key Object data
5. `verify_webhook_signature (VerifyWebhookSignature)`: hàm/phương thức kiểm tra chữ ký số được tạo dựa trên dữ liệu webhook có giống với chữ ký số được Casso gửi tới hay không.