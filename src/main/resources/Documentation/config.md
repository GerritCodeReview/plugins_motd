MOTD Configuration
==================

Reloading Messages
------------------

To reload the current messages displayed to clients, simply reload the plugin:

```
  ssh -p 29418 localhost gerrit plugin reload motd
```

File `motd.config`
------------------

The file `$site_path/etc/motd.config` is a Git-style config file that contains
the messages and subnet-based messages to be displayed to users.  It contains
an optional global message, and optional sections for subnet-specific messages.

Subnet-based configurations should replace the `SUBNET` portion with the
appropriate subnet in CIDR-style notation.

Optionally, the parameter `${user}` will be substituted into any message as
the user's username.  If user is unauthenticated, the substitution will not
be performed.

gerrit.motd
:	Message to be displayed to all users, regardless of their IP.

subnet.SUBNET.motd
:	Message to be displayed to users with an IP in range of `SUBNET`.