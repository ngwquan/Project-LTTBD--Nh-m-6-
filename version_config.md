\*\*\*Cấu hình trong file lib.versions.toml:



1. Trong folder gradle mở file libs.versions.toml

2\. Tìm 2 dòng bên dưới

&#x09;kotlin = "2.2.20"

&#x09;ksp = "2.2.20-2.0.4"

3\. Sau đó sửa thành version kotlin đang dùng

4\. Tìm kiếm version ksp phù hợp với kotlin đang dùng và sửa version của ksp

&#x09;[https://github.com/google/ksp/releases?](https://github.com/google/ksp/releases?)

&#x20;   Ví dụ bạn sử dụng kotlin "2.1.10" thì sẽ thấy version "2.1.10-1.0.31"

&#x09;[https://github.com/google/ksp/releases?page=4](https://github.com/google/ksp/releases?page=4)





\*\*\*Cấu hình trong file build.gradle.kts dự án (bên ngoài app):



&#x09;Sửa thành version ksp phù hợp mà bạn đã tìm trước đó

&#x09;id("com.google.devtools.ksp") version "2.2.20-2.0.4" apply false





\*\*\*File build.gradle.kts module (bên trong app) không cần sửa

