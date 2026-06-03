import Server from "@/components/main/Server";
import Serverless from "@/components/main/Serverless";
import HeroTitle from "@ui/HeroTitle";
import HeroSubtitle from "@ui/HeroSubtitle";
import P from "@ui/P";

function Main() {
    return (
        <main className="flex justify-center px-6 py-8 grow bg-white dark:bg-gray-800 shadow-md ring ring-gray-900/5">
            <div className="flex flex-col gap-4 ">
                <HeroTitle>Block Break</HeroTitle>
                <HeroSubtitle>How many blocks can you break</HeroSubtitle>
                <div className="flex flex-wrap gap-4">
                    <Server />
                    <Serverless />
                </div>

                <P>
                    Showcase web and gpu software development resources using
                    server and serverless infrastructures.
                </P>

                <P>Handcrafted with care by Anthony Mills.</P>
            </div>
        </main>
    );
}

export default Main;
