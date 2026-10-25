import http from 'k6/http';
import { Counter, Trend } from 'k6/metrics';

const URL = 'http://localhost:8080/api/users/cache/1/with-cachestamped-protector';
const DURATION_SECONDS = 30;
const CONCURRENCY = 50;

const successCount = new Counter('success_count');
const failureCount = new Counter('failure_count');
const requestDuration = new Trend('request_duration', true);

export const options = {
  scenarios: {
    load_test: {
      executor: 'constant-vus',
      vus: CONCURRENCY,
      duration: `${DURATION_SECONDS}s`,
    },
  },
  thresholds: {},
};

export default function () {
  const response = http.get(URL, {
    timeout: '10s',
    tags: { name: URL },
  });

  requestDuration.add(response.timings.duration);

  if (response.status === 200) {
    successCount.add(1);
  } else {
    failureCount.add(1);
  }
}

export function handleSummary(data) {
  const metrics = data.metrics;
  const total = metrics.http_reqs?.values.count ?? 0;
  const success = metrics.success_count?.values.count ?? 0;
  const failure = total - success;

  const avg = metrics.request_duration?.values.avg ?? 0;
  const p95 = metrics.request_duration?.values['p(95)'] ?? 0;

  const statusCodes = {};

  for (const metric of Object.values(data.metrics)) {
    if (metric.type !== 'counter' || !metric.contains) continue;
  }

  const elapsed = data.state.testRunDurationMs / 1000;

  const output = [
    `테스트 설정 시간: ${DURATION_SECONDS}s`,
    `총 요청: ${total}`,
    `성공: ${success}, 실패: ${failure}`,
    `총 소요 시간(요청 정리 포함): ${elapsed.toFixed(2)}s`,
    `처리량: ${(total / DURATION_SECONDS).toFixed(2)} req/s`,
    `평균 응답 시간: ${avg.toFixed(2)} ms`,
    `P95 응답 시간: ${p95.toFixed(2)} ms`,
  ];

  return {
    stdout: output.join('\n') + '\n',
  };
}