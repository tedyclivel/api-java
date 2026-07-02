import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://api-java-s7u2.onrender.com/:path*',
      },
    ];
  },
};

export default nextConfig;
