This plugin can output messages to clients when pulling/fetching/cloning
code from Gerrit Code Review.  If the client (and transport mechanism)
can support sending the message to the client, it will be displayed to
the user (usually prefixed by "remote: "), but will be silently discarded
otherwise.

Messages may be configured for either the entire server, or based on the
subnet of the client connecting.

