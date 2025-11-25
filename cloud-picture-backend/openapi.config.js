import fs from 'fs';
import fetch from 'node-fetch';
import { generateService } from '@umijs/openapi';

const SCHEMA_URL = 'http://localhost:8123/api/v2/api-docs'; // 或 /v3/api-docs
const LOCAL_SCHEMA_FILE = 'E:\\vscode\\PlantProject\\cloudPicture\\cloud-picture-backend\\swagger.json';

async function main() {
  // 1. 拉取 Swagger JSON
  const res = await fetch(SCHEMA_URL);
  const schema = await res.json();

  // 2. 检查 info.version
  if (!schema.info) {
    schema.info = {};
  }
  if (!schema.info.version) {
    console.log('info.version 不存在，自动补 1.0.0');
    schema.info.version = '1.0.0';
  }

  // 3. 保存本地
  fs.writeFileSync(LOCAL_SCHEMA_FILE, JSON.stringify(schema, null, 2));

  // 4. 使用 @umijs/openapi 生成服务
  generateService({
    requestLibPath: "import request from '@/request'",
    schemaPath: LOCAL_SCHEMA_FILE,
    serversPath: "./src",
  });

  console.log('✅ 服务生成完成');
}

main().catch(err => {
  console.error(err);
});
