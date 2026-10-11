/**
 * Gets the database specific WebSocket URL.  Uses query parameter'db'.
 *
 * db: 0 Random choice of MariaDB/MySQL, MongoDB, or PostgreSQL
 * db: 1 MariaDB/MySQL
 * db: 2 MongoDB
 * db: 3 PostgreSQL
 * default: Serverless
 *
 * @returns The WebSocket url to connect to
 *
 */

export function getWebSocketUrl(db: string | null): [string, string] {
  const urls: [string, string][] = [
      ['wss://socket.serverblockbreak.ca:443/mariadb', 'Server/MariaDB'],
      ['wss://socket.serverblockbreak.ca:443/mongodb', 'Server/MongoDB'],
      ['wss://socket.serverblockbreak.ca:443/postgresql', 'Server/PostgreSQL']
  ];

  const r = Math.floor(Math.random() * urls.length);

  switch (db) {
      case '0':
          return urls[r];
      case '1':
          return urls[0];
      case '2':
          return urls[1];
      case '3':
          return urls[2];
      default:
          return ['wss://serverless.blockbreak.ca:443', "Serverless"];
      }
}
