{ pkgs }: {
    deps = [
      pkgs.watchexec
      pkgs.xorg.setxkbmap
        pkgs.graalvm-ce
        pkgs.maven
    ];
}