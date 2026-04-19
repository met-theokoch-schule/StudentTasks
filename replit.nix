{ pkgs }: {
    deps = [
      pkgs.xorg.setxkbmap
        pkgs.graalvm-ce
        pkgs.maven
    ];
}