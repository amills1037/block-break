import { getWebSocketUrl } from "./websocketurl";

describe("getWebSocketUrl", () => {
    it("returns serverless url", () => {
        const url = getWebSocketUrl(null);
        expect(url[0]).toEqual("wss://serverless.blockbreak.ca:443");
        expect(url[1]).toEqual("Serverless");
    });

    it("returns random url", () => {
        const url = getWebSocketUrl("0");
        expect(url[0]).toMatch(/^wss:\/\/socket.serverblockbreak.ca:443\//);
        expect(url[1]).toMatch(/^Server\//);
    });

    it("returns MariaDB url", () => {
        const url = getWebSocketUrl("1");
        expect(url[0]).toEqual("wss://socket.serverblockbreak.ca:443/mariadb");
        expect(url[1]).toEqual("Server/MariaDB");
    });

    it("returns MongoDB url", () => {
        const url = getWebSocketUrl("2");
        expect(url[0]).toEqual("wss://socket.serverblockbreak.ca:443/mongodb");
        expect(url[1]).toEqual("Server/MongoDB");
    });

    it("returns PostgreSQL url", () => {
        const url = getWebSocketUrl("3");
        expect(url[0]).toEqual("wss://socket.serverblockbreak.ca:443/postgresql");
        expect(url[1]).toEqual("Server/PostgreSQL");
    });
});
