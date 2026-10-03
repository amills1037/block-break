import ServerButton from "@/components/main/ServerButton";
import H2 from "@ui/H2";
import HeroBox from "@ui/HeroBox";
import P from "@ui/P";

function Server() {
    return (
        <HeroBox>
            <H2>Server</H2>
            <P>
                <div className="h-40">
                    Choose this version to see the Kubernetes version in action.
                    The server version uses Spring and MySQL, PostgreSQL, or
                    MongoDB as the database. You can also choose a specific
                    database.
                </div>
            </P>
            <ServerButton />
            <ServerButton db="mysql" />
            <ServerButton db="postgresql" />
            <ServerButton db="mongodb" />
        </HeroBox>
    );
}

export default Server;
