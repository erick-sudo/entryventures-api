# Policy
Policies to let specification of who has access to a resource and what actions they can perform on that resource.

## Types of policies
### Identity Based Policy
Policy attached to an IAM user

### Resource Based Policy
Policy attached to a resource to control how it is accessed

## Properties
- Effect - Whether to allow or not: (Allow/Deny)
- Resource - a server resource subject to defined action
- Action - Action to be managed by a policy: Array
- Identities - Who can access resource

## Conditional Operators

```yaml
# Policy template
policy:
  - Pid: PolicyId
  - Resource:
      - resource_ARN1
      - resource_ARN2
      - resource_ARN3
  - Effect: Allow/Deny
  - Action:
      - Action1ARN
      - Action2ARN
      - Action3ARN
  - Condition:
      - role:ADMIN
      - group:ADMIN
```