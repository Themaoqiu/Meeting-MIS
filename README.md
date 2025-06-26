# 📅 会议室预订系统

该项目作为上海电力大学信管Java课设

项目后端基于 Spring Boot 构建，前端采用 Vue.js，实现了流畅的单页应用（SPA）交互

## ✨ 核心功能

### 👤 用户认证系统
- 安全可靠的用户注册、登录及会话管理

### 🔐 权限控制
- **普通用户**：管理自己的会议预订
- **会议室管理员**：管理会议室及设备
- **系统管理员**：拥有最高权限，管理所有用户和角色

### 🏢 会议室管理
- 会议室/设备的全生命周期管理

### 🗓️ 交互式日程视图
- 提供功能完整的日历看板，支持按月或按天查看所有会议室的日程安排
- 防止时间冲突

### ✅ 智能化预订
- 用户在创建新预订时，可指定会议主题、参会人数及所需设备。系统会实时校验会议室容量和时间可用性

### 📊 多维度数据看板
- 状态看板: 实时展示所有会议室的当前状态（可用 ✅、占用中 🔴、维护中 🟡）
- 统计报告: 可生成并查看指定月份的详细使用报告，包括会议室使用率、总预订次数、取消率和用户活跃度排名等

### 🔔 自动提醒
- 会议开始前自动通知

## 🛠️ 技术栈

### 前端
- Vue 3 + TypeScript
- Tailwind CSS + Shadcn-vue
- Vite
  
```tree
frontend
├── .vscode                      
├── public                     
├── src
│   ├── components
│   │   ├── ui                   // 安装的shadn组件 
│   │   └── ...                  // 自定义的视图组件
│   ├── layouts                  // 布局设置
│   ├── lib                      
│   ├── router                   // 路由配置 
│   ├── services                 // API服务 (与后端接口交互)
│   ├── stores                   // 状态管理 (Pinia)
│   └── views                    // 页面视图组件
├── .gitignore                   // Git忽略文件配置
├── index.html                   // 应用主HTML文件
├── package.json                 // 项目元数据和依赖项
├── tsconfig.json                // TypeScript 编译器配置
└── vite.config.ts               // Vite 构建工具配置
```

### 后端
- Spring Boot 3
- Java 21
- MySQL

 ```tree
backend
├── .mvn                         // Maven 配置
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cn/edu/shiep/backend
│   │   │       └── meetingroom
│   │   │           ├── config           // 安全配置 SecurityConfig
│   │   │           ├── controller       // 处理HTTP请求
│   │   │           ├── dto              // 数据传输对象 用于API请求和响应
│   │   │           ├── entity           // JPA实体，对应数据库表映射
│   │   │           ├── enums            // 用户角色 通知状态等的枚举类型 
│   │   │           ├── repository       // JPA数据库操作接口
│   │   │           ├── security         // Spring Security所需的信息传递配置
│   │   │           ├── service          // 业务逻辑服务层
│   │   │           └── utils            // 
│   │   └── resources                    // spring配置文件
│   └── test                             // 测试代码
└── pom.xml                              // Maven项目对象模型文件
```

## 🚀 部署指南

### 环境要求
- JDK 21+
- Node.js 16+
- MySQL 8+

### 后端部署
```bash
git clone https://github.com/Themaoqiu/Meeting-MIS.git
cd backend
mvn spring-boot:run
```

### 前端部署
```bash
cd frontend
npm install
npm run dev
```

## 🔑 默认会议管理员账号
- 邮箱: admin@meeting.com  
- 密码: admin123

访问地址:
- 前端: http://localhost:5173
- 后端: http://localhost:8080