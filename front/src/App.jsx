import React, { useEffect, useState, useRef } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  Legend,
  CartesianGrid,
} from "recharts";

const App = () => {
  const [data, setData] = useState([]);
  const ws = useRef(null);
  const intervalRef = useRef(null);

  useEffect(() => {
    // WebSocket 연결
    ws.current = new WebSocket("ws://localhost:8080/ws/rates");

    ws.current.onopen = () => {
      console.log("WebSocket Connected");

      // 최초 1회 요청
      ws.current.send("REQUEST_RATES");

      // 60초마다 서버에 "REQUEST_RATES" 메시지 보내기
      intervalRef.current = setInterval(() => {
        if (ws.current.readyState === WebSocket.OPEN) {
          console.log("⏱ 5초 경과 → 서버에 환율 요청");
          ws.current.send("REQUEST_RATES");
        }
      }, 5000);
    };

    ws.current.onmessage = (event) => {
      try {
        const rate = JSON.parse(event.data);
        const time = new Date().toLocaleTimeString();

        setData((prev) => [
          ...prev.slice(-40),
          {
            time,
            KRW: rate.KRW,
            JPY: rate.JPY,
            EUR: rate.EUR,
          },
        ]);
      } catch (e) {
        console.error("Invalid WebSocket data", event.data);
      }
    };

    ws.current.onerror = (e) => console.error("WS Error", e);

    // cleanup
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      if (ws.current) ws.current.close();
    };
  }, []);

  return (
    <div
      style={{
        background: "#0d1117",
        minHeight: "100vh",
        padding: "30px",
        color: "white",
      }}
    >
      <h1 style={{ marginBottom: "20px" }}>💹 실시간 환율 차트</h1>

      <div
        style={{
          background: "#161b22",
          padding: "20px",
          borderRadius: "12px",
          boxShadow: "0 4px 20px rgba(0,0,0,0.3)",
        }}
      >
        <ResponsiveContainer width="100%" height={420}>
          <LineChart data={data} margin={{ top: 40, right: 40, left: 0, bottom: 10 }}>
            <defs>
              <linearGradient id="colorKRW" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#4caf50" stopOpacity={0.9} />
                <stop offset="100%" stopColor="#4caf50" stopOpacity={0.1} />
              </linearGradient>

              <linearGradient id="colorJPY" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#ff9800" stopOpacity={0.9} />
                <stop offset="100%" stopColor="#ff9800" stopOpacity={0.1} />
              </linearGradient>

              <linearGradient id="colorEUR" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#03a9f4" stopOpacity={0.9} />
                <stop offset="100%" stopColor="#03a9f4" stopOpacity={0.1} />
              </linearGradient>
            </defs>

            <XAxis dataKey="time" tick={{ fill: "#bbb" }} />
            <YAxis tick={{ fill: "#bbb" }} />
            <Tooltip
              contentStyle={{
                background: "#1e2329",
                border: "none",
                borderRadius: "8px",
              }}
              labelStyle={{ color: "#fff" }}
            />
            <Legend wrapperStyle={{ color: "#fff" }} />
            <CartesianGrid strokeDasharray="3 3" stroke="#30363d" />

            <Line
              type="monotone"
              dataKey="KRW"
              stroke="#4caf50"
              strokeWidth={2.5}
              dot={false}
              activeDot={{ r: 6 }}
            />
            <Line
              type="monotone"
              dataKey="JPY"
              stroke="#ff9800"
              strokeWidth={2.5}
              dot={false}
              activeDot={{ r: 6 }}
            />
            <Line
              type="monotone"
              dataKey="EUR"
              stroke="#03a9f4"
              strokeWidth={2.5}
              dot={false}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default App;
